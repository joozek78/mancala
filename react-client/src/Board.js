import React from 'react';
import './Board.css';

class Board extends React.Component {

    constructor(props) {
        super(props);
    }

    render() {
        return (
            <div className="mancala-board">
                {this.renderStore("blue", 13)}
                {this.renderPit("blue", 12)}
                {this.renderPit("blue", 11)}
                {this.renderPit("blue", 10)}
                {this.renderPit("blue", 9)}
                {this.renderPit("blue", 8)}
                {this.renderPit("blue", 7)}
                {this.renderStore("red", 6)}
                {this.renderPit("red", 0)}
                {this.renderPit("red", 1)}
                {this.renderPit("red", 2)}
                {this.renderPit("red", 3)}
                {this.renderPit("red", 4)}
                {this.renderPit("red", 5)}
            </div>
        );
    }

    renderStore(player, slotId) {
        const classes = "mancala-store mancala-store-" + player;
        return (<div className={classes}>{this.props.slots[slotId]}</div>);
    }
    renderPit(player, slotId) {
        const classes = "mancala-pit mancala-pit-" + player;
        return <div className={classes} onClick={() => this.props.slotClicked(slotId)}>{this.props.slots[slotId]}</div>;
    }
}

export default Board;
