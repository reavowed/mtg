import {useContext} from "preact/hooks";
import DecisionMaker from "../../contexts/DecisionMaker";
import GameState from "../../contexts/GameState";

export default function UndoButton() {
    const gameState = useContext(GameState);
    const decisionMaker = useContext(DecisionMaker);
    return gameState.canUndoLastChoice && <button type="button" className="btn btn-primary ml-2" onClick={decisionMaker.requestUndo}>Undo</button>;
}
