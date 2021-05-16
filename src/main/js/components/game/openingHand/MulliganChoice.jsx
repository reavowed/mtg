import {useContext} from "preact/hooks";
import GameState from "../../../GameState";
import {getPlural} from "../../../utils/word-helpers";
import HorizontalCenter from "../../layout/HorizontalCenter";
import DecisionButton from "../DecisionButton";
import Hand from "../Hand";

export default function MulliganChoice() {
    const gameState = useContext(GameState);
    const numberOfCardsToKeep = 7 - gameState.currentChoice.details.mulligansSoFar;
    if (gameState.player === gameState.currentChoice.playerToAct) {
        return <div>
            <Hand />
            <h1 className="text-center mt-3">Keep {getPlural(numberOfCardsToKeep, "card", "cards")} or mulligan to {getPlural(numberOfCardsToKeep - 1, "card", "cards")}?</h1>
            <HorizontalCenter>
                <DecisionButton optionToChoose="K" text="Keep"/>
                <DecisionButton optionToChoose="M" text="Mulligan"/>
            </HorizontalCenter>
        </div>
    } else {
        return <h1 className="text-center mt-3">Waiting for your opponent to make a mulligan decision</h1>
    }
}
