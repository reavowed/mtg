import {useContext} from "preact/hooks";
import DecisionMaker from "../../../DecisionMaker";
import GameState from "../../../GameState";
import HorizontalCenter from "../../layout/HorizontalCenter";

export default function MulliganDecision() {
    const gameState = useContext(GameState);
    const decisionMaker = useContext(DecisionMaker);
    if (gameState.player === gameState.currentChoice.playerToAct) {
        return <div>
            <h1 className="text-center mt-3">Keep this hand or mulligan to 6 cards?</h1>
            <HorizontalCenter>
                <button type="button"
                        className="btn btn-primary btn-lg"
                        disabled={decisionMaker.requestInProgress}
                        onclick={() => decisionMaker.makeDecision("K")}
                >Keep</button>
                <button type="button" className="btn btn-primary btn-lg ml-2" disabled={decisionMaker.requestInProgress}>Mulligan</button>
            </HorizontalCenter>
        </div>
    } else {
        return <h1 className="text-center mt-3">Waiting for your opponent to make a mulligan decision</h1>
    }
}
