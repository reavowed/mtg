import {useContext} from "preact/hooks";
import DecisionMaker from "../../../DecisionMaker";
import GameState from "../../../GameState";
import {getPlural} from "../../../utils/word-helpers";
import HorizontalCenter from "../../layout/HorizontalCenter";

export default function MulliganDecision() {
    const gameState = useContext(GameState);
    const decisionMaker = useContext(DecisionMaker);
    const cardsToKeep = 7 - gameState.currentChoice.details.mulligansSoFar;
    if (gameState.player === gameState.currentChoice.playerToAct) {
        return <div>
            <h1 className="text-center mt-3">Keep {getPlural(cardsToKeep, "card", "cards")} or mulligan to {getPlural(cardsToKeep - 1, "card", "cards")}?</h1>
            <HorizontalCenter>
                <button type="button"
                        className="btn btn-primary btn-lg"
                        disabled={decisionMaker.requestInProgress}
                        onclick={() => decisionMaker.makeDecision("K")}
                >Keep</button>
                <button type="button"
                        className="btn btn-primary btn-lg ml-2"
                        disabled={decisionMaker.requestInProgress}
                        onclick={() => decisionMaker.makeDecision("M")}
                >Mulligan</button>
            </HorizontalCenter>
        </div>
    } else {
        return <h1 className="text-center mt-3">Waiting for your opponent to make a mulligan decision</h1>
    }
}
