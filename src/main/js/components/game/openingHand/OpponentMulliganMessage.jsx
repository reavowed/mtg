import _ from "lodash";
import {useContext} from "preact/hooks";
import GameState from "../../../GameState";
import {getPlural} from "../../../utils/word-helpers";

function testCurrentChoice(gameState, type, playerId) {
    return gameState.currentChoice.type === type && gameState.currentChoice.playerToAct === playerId;
}

function getOpponentMulliganMessage(gameState) {
    const opponentId = _.filter(gameState.playersInTurnOrder, p => p !== gameState.player)[0];
    const opponentMulliganState = gameState.mulliganState[opponentId];
    if (opponentMulliganState.hasKept) {
        const keepMessage = "Your opponent has kept a " + (7 - opponentMulliganState.mulligansTaken) + " card hand";
        if (testCurrentChoice(gameState, "ReturnCardsToLibraryChoice", opponentId)) {
            return keepMessage + " and is choosing " + getPlural(opponentMulliganState.mulligansTaken, "card", "cards") + " to put back";
        } else {
            return keepMessage;
        }
    } else {
        if (opponentMulliganState.mulligansTaken === 0) {
            return "Your opponent is considering a 7 card hand";
        } else {
            return "Your opponent has mulliganed to a " + (7 - opponentMulliganState.mulligansTaken) + " card hand";
        }
    }
}

export default function OpponentMulliganMessage() {
    const gameState = useContext(GameState);
    return <h1 className="text-center mt-3">{getOpponentMulliganMessage(gameState)}</h1>
}
