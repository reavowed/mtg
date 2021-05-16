import {h} from 'preact';
import _ from 'lodash';
import {useEffect, useState} from "preact/hooks";
import ScryfallService from "./ScryfallService";
import {getJson} from "./utils/fetch-helpers";

const scryfallService = new ScryfallService();

function Card({card}) {
    const [scryfallCard, setScryfallCard] = useState(null);
    useEffect(() => scryfallService.requestCard(card, setScryfallCard), []);
    return scryfallCard && <img src={scryfallCard.image_uris.small} className="mx-1" />
}

function Hand({objects}) {
    return _.map(objects, object => <Card card={object}/>);
}

function FirstPlayerMessage({gameState}) {
    return <h1 class="text-center mb-3">
        {(gameState.player === gameState.playersInTurnOrder[0]) ? "You go first" : "Your opponent goes first"}
    </h1>
}

function getPlural(number, singularWord, pluralWord) {
    if (number === 1) {
        return "a " + singularWord;
    } else {
        return number + " " + pluralWord;
    }
}

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

function OpponentMulliganMessage({gameState}) {
    return <h1 class="text-center mt-3">{getOpponentMulliganMessage(gameState)}</h1>
}

function MulliganDecision({gameState}) {
    if (gameState.player === gameState.currentChoice.playerToAct) {
        return <h1 class="text-center mt-3">Keep this hand or mulligan to 6 cards?</h1>
    } else {
        return <h1 class="text-center mt-3">Waiting for your opponent to make a mulligan decision</h1>
    }
}

function OpeningHand({gameState}) {
    const mulliganMessage = (gameState.player === gameState.currentChoice.playerToAct) ?
        "Keep this hand or mulligan to 6 cards?" :
        "Waiting for your opponent to make a mulligan decision";
    return <div>
        <FirstPlayerMessage gameState={gameState} />
        <Hand objects={gameState.hand}/>
        <OpponentMulliganMessage gameState={gameState} />
        <MulliganDecision gameState={gameState} />
    </div>;
}

export default function Game() {
    const [gameState, setGameState] = useState(null);
    useEffect(() => {
        getJson('$currentPath/state').then(setGameState)
    }, []);

    const content = gameState ?
        <OpeningHand gameState={gameState}/> :
        <div className="spinner-border"/>;
    return <div class="d-flex flex-column justify-content-center vh-100">
        <div class="d-flex justify-content-center">
            {content}
        </div>
    </div>;
}
