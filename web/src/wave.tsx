let buffer: Float32Array[] = []
let length: number = 0

const bufferSize = 4096

type RecordingCallback = (blob: Blob) => void
type StreamSourceCreator = (mediaStream: MediaStream) => MediaStreamAudioSourceNode
type ScriptProcessorCreator = (mediaStreamAudioSource: MediaStreamAudioSourceNode) => ScriptProcessorNode
type AudioEventProcessorCreator = (scriptProcessor: ScriptProcessorNode) => void

export let stream: MediaStream

export function startRecording(callback: RecordingCallback) {
  createStream()
    .then(createStreamSource())
    .then(createScriptProcessor())
    .then(createAudioEventProcessor(callback))
}

function createStream(): Promise<MediaStream> {
  const settings = {
    audio: true
  }

  // chrome only
  return navigator.mediaDevices.getUserMedia(settings)
}

function createStreamSource(): StreamSourceCreator {
  return (mediaStream: MediaStream) => {
    stream = mediaStream
    const context = new AudioContext()
    return context.createMediaStreamSource(mediaStream)
  }
}

function createScriptProcessor(): ScriptProcessorCreator {
  return (mediaStreamAudioSource: MediaStreamAudioSourceNode) => {
    const channels = 1
    const processor = mediaStreamAudioSource.context.createScriptProcessor(bufferSize, channels, channels)

    mediaStreamAudioSource.connect(processor)
    processor.connect(mediaStreamAudioSource.context.destination)

    return processor
  }
}

function createAudioEventProcessor(callback: RecordingCallback): AudioEventProcessorCreator {
  return (scriptProcessor: ScriptProcessorNode) => {
    //TODO: refactor deprecated API
    scriptProcessor.onaudioprocess = (audioProcessingEvent: AudioProcessingEvent) => {
      const channel = 0
      const data = new Float32Array(bufferSize)

      // copy data out so as not to reuse underlying memory
      // https://stackoverflow.com/questions/59252870/obtaining-microphone-pcm-data-from-getchanneldata-method-using-webaudio-api-does
      audioProcessingEvent.inputBuffer.copyFromChannel(data, channel)

      save(data)

      if (length > 10 * 1000) {
        const blob = samplesToBlob(buffer, length, scriptProcessor.context.sampleRate)

        clear()
        callback(blob)
      }
    }
  }
}

function save(data: Float32Array) {
  buffer.push(data)
  length += data.length
}

function clear() {
  buffer = []
  length = 0
}

function samplesToBlob(buffer: Float32Array[], length: number, recordRate: number): Blob {
  //records at 48000
  const exportRate = 16000
  const merged = mergeBuffer(buffer, length)
  const sampled = downsample(merged, recordRate,exportRate)
  const encoded = encode(sampled)

  return new Blob([encoded])
}

function mergeBuffer(buffer: Float32Array[], length: number) {
  const result = new Float32Array(length)
  let offset = 0

  buffer.forEach(sample => {
    result.set(sample, offset)
    offset += sample.length
  })

  return result
}

function downsample(buffer: Float32Array, sampleRate: number, exportSampleRate: number) {
  if (exportSampleRate === sampleRate) {
    return buffer
  }

  const sampleRateRatio = sampleRate / exportSampleRate
  const newLength = Math.round(buffer.length / sampleRateRatio)
  const result = new Float32Array(newLength)

  let offsetResult = 0
  let offsetBuffer = 0

  while (offsetResult < result.length) {
    const nextOffsetBuffer = Math.round((offsetResult + 1) * sampleRateRatio)
    let accum = 0, count = 0
    for (let i = offsetBuffer; i < nextOffsetBuffer && i < buffer.length; i++) {
      accum += buffer[i]
      count++
    }
    result[offsetResult] = accum / count
    offsetResult++
    offsetBuffer = nextOffsetBuffer
  }

  return result
}

function encode(samples: Float32Array) {
  const buffer = new ArrayBuffer(samples.length * 2)
  const view = new DataView(buffer)

  for (let i = 0; i < samples.length; i++) {
    let s = Math.max(-1, Math.min(1, samples[i]))
    view.setInt16(i * 2, s < 0 ? s * 0x8000 : s * 0x7FFF, true)
  }

  return view
}