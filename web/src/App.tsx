import React from 'react';
import './App.css';
import { Joystick } from 'react-joystick-component';
import { IJoystickUpdateEvent } from "react-joystick-component/build/lib/Joystick";
import axios from 'axios';

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
    </div>
  );
}

function start() {
  let video = document.getElementById("video") as HTMLImageElement
  let audio = document.getElementById("audio") as HTMLAudioElement

  video.src = "/video"
  audio.src = "/audio"
  audio.play()

  audio.currentTime += 5

  let start = document.getElementById("start") as HTMLAnchorElement

  start.hidden = true
  audio.hidden = true

  return false
}

function handleMove(event: IJoystickUpdateEvent) {
  axios.get(`/robot/${event.x}/${event.y}`)
}

function handleStop(event: IJoystickUpdateEvent) {
  axios.get("/robot/0/0")
}

export default App;
