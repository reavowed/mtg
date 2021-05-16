import {useContext} from "preact/hooks";
import GameState from "../../../GameState";

export default function FirstPlayerMessage() {
    const gameState = useContext(GameState);
    return <h1 className="text-center mb-3">
        {(gameState.player === gameState.gameData.playersInTurnOrder[0]) ? "You go first" : "Your opponent goes first"}
    </h1>
}
