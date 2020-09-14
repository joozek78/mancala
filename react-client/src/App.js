import React from 'react';
import Board from './Board';
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';
import { v4 as uuidv4 } from 'uuid';

class App extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      score: {
        red: 0,
        blue: 0
      },
      finished: false,
      slots: Array(14).fill(0),
      currentPlayer: "",
      error: "",
      started: false,
    };
  }

  componentDidMount() {
    const ws = new SockJS('http://localhost:8080/game-ws');
    this.client = Stomp.over(ws);
    this.client.connect({}, () => this.onConnected());
  }

  componentWillUnmount() {
    this.client.disconnect();
  }

  onConnected() {
    this.clientId = uuidv4();
    this.client.subscribe('/game/state', (msg) => this.onStateReceived(msg));
    this.client.subscribe('/game/error/' + this.clientId, (msg) => this.onErrorReceived(msg));
  }

  onStateReceived(frame) {
    const gameStateMsg = JSON.parse(frame.body);
    this.setState({
      slots: gameStateMsg.gameState.slots,
      finished: gameStateMsg.score.finished,
      currentPlayer: gameStateMsg.gameState.currentPlayer,
      score: {
        red: gameStateMsg.score.redScore,
        blue: gameStateMsg.score.blueScore
      }
    });
  }

  onErrorReceived(frame) {
    const errorMessage = JSON.parse(frame.body);
    console.error("Received error: ", errorMessage);
    this.setState({
      error: errorMessage.details
    });
  }

  newGame() {
    this.send("/game/new", {});
  }

  joinGame(player) {
    this.setState({ "me": player, "started": true });
    this.send("/game/join", {});
  }

  handleMove(slotId) {
    this.send("/game/move", { "from": slotId, "player": this.state.me });
  }

  send(destination, payload) {
    this.setState({"error":null});
    this.client.send(destination, { "client-id": this.clientId }, JSON.stringify(payload));
  }

  render() {
    if (!this.state.started) {
      return (
        <div>
          <button onClick={() => this.joinGame('red')}>Join the game as Red</button>
          <button onClick={() => this.joinGame('blue')}>Join the game as Blue</button>
        </div>
      );
    } else {
      return (
        <div>
          <Board slots={this.state.slots} slotClicked={s => this.handleMove(s)} />
          <button onClick={() => this.joinGame('red')}>Join the game as Red</button>
          <button onClick={() => this.joinGame('blue')}>Join the game as Blue</button>
          <button onClick={() => this.newGame()}>Reset the game</button>
          <p>You're playing as {this.state.me}</p>
          {this.renderCurrentPlayer()}
          {this.renderScore()}
          {this.renderWinner()}
          {this.renderError()}
        </div>
      );
    }
  }
  
  renderWinner() {
    if (this.state.finished) {
      const tie = this.state.score.red === this.state.score.blue;
      const winner = this.state.score.red > this.state.score.blue ? "Red" : "Blue";
      if (tie) {
        return <p>The game has ended with a tie</p>;
      }
      return <p>{winner} won</p>
    }
  }

  renderScore() {
    return <p>Red: {this.state.score.red}, Blue: {this.state.score.blue}</p>
  }

  renderError() {
    if (!this.state.error) {
      return;
    }
    return <p>Error: {this.state.error}</p>
  }

  renderCurrentPlayer() {
    if (this.state.finished) {
      return;
    }
    return <p>Current player: {this.state.currentPlayer}</p>
  }
}

export default App;
