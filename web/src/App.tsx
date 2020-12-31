import React from 'react';
import './App.css';
import { Joystick } from 'react-joystick-component';
import { IJoystickUpdateEvent } from "react-joystick-component/build/lib/Joystick";
import axios from 'axios';
import { w3cwebsocket as ws } from "websocket";
import { startRecording } from "./wave"

function App() {
  return (
    <div className="App">
      <div id="start" className="Start">
        <a onClick={start} href="#">&#x25b6;</a>
      </div>
      <div className="App-header">
        <img id="video" className="Video" />
        <audio id="audio" className="Audio" />
      </div>
      <div className="Joystick">
        <Joystick size={100} move={handleMove} stop={handleStop} />
      </div>
      <div className="MicrophoneWaveform">
        <span className="WaveformLabel"><span className="iconify" data-icon="mdi:web" data-inline="false"></span> 48,000.0 Hz, 16 Bit, mono, 2 bytes/frame</span><br/>
        <img id="microphoneWaveform" />
      </div>
      <div className="AudioWaveform">
        <span className="WaveformLabel"><span className="iconify" data-icon="mdi-robot" data-inline="false"></span> 16,000.0 Hz, 16 Bit, stereo, 2 bytes/frame</span><br/>
        <img id="audioWaveform" />
      </div>
    </div>
  );
}

function start() {
  streamMicrophone()

  let video = document.getElementById("video") as HTMLImageElement
  let audio = document.getElementById("audio") as HTMLAudioElement

  let microphoneWaveform = document.getElementById("microphoneWaveform") as HTMLImageElement
  let audioWaveform = document.getElementById("audioWaveform") as HTMLImageElement

  video.src = "/video"
  audio.src = "/audio"
  audio.play()

  microphoneWaveform.src = "/microphone/waveform"
  microphoneWaveform.style.visibility = "visible"
  audioWaveform.src = "/audio/waveform"
  audioWaveform.style.visibility = "visible"

  let labels = document.getElementsByClassName("WaveformLabel")
  for (let i = 0; i < labels.length; i++) {
    // @ts-ignore
    labels.item(i).style.visibility = "visible"
  }

  let joysticks = document.getElementsByClassName("Joystick")
  for (let i = 0; i < joysticks.length; i++) {
    // @ts-ignore
    joysticks.item(i).style.visibility = "visible"
  }

  audio.currentTime += 5

  let start = document.getElementById("start") as HTMLAnchorElement

  start.hidden = true
  audio.hidden = true

  return false
}

function streamMicrophone() {
  const client = new ws('wss://piankabot.lan:8443/microphone');
  client.onclose = (event) => {
    console.log(event)
  }
  client.onerror = (event) => {
    console.error(event)
  }
  client.onopen = () => startRecording((blob: Blob) => blob.arrayBuffer().then((array) => client.send(array)))
}

function handleMove(event: IJoystickUpdateEvent) {
  axios.get(`/robot/${event.x}/${event.y}`)
}

function handleStop(event: IJoystickUpdateEvent) {
  axios.get("/robot/0/0")
}

export default App;
