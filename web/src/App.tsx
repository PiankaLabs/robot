import React from 'react';
import './App.css';
import { Joystick } from 'react-joystick-component';
import { IJoystickUpdateEvent } from "react-joystick-component/build/lib/Joystick";
import axios from 'axios';

function App() {
  return (
    <div className="App">
      <div className="App-header">
        <img src="/video" className="Video" alt="" />
      </div>
      <div className="Joystick">
        <Joystick size={100} move={handleMove} stop={handleStop} />
      </div>
    </div>
  );
}

function handleMove(event: IJoystickUpdateEvent) {
  axios.get(`/robot/${event.x}/${event.y}`)
}

function handleStop(event: IJoystickUpdateEvent) {
  axios.get("/robot/0/0")
}

export default App;
