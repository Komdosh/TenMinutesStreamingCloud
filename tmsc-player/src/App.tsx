import React from 'react';
import logo from './logo.svg';
import './App.css';
import ReactPlayer from 'react-player';

function App() {
  return (
      <div className="App">
        <header className="App-header">
          <img src={logo} className="App-logo" alt="logo"/>
          <ReactPlayer controls height='400'
                       url={[
                         {src: 'http://localhost:8002/streaming/videos', type: 'video/mp4'}
                       ]}/>
        </header>
      </div>
  );
}

export default App;
