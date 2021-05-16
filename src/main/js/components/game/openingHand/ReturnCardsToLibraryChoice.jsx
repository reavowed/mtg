import _ from "lodash";
import {useContext, useState} from "preact/hooks";
import GameState from "../../../GameState";
import HorizontalCenter from "../../layout/HorizontalCenter";
import Card from "../Card";
import CardBack from "../CardBack";
import DecisionButton from "../DecisionButton";

function areCardsEqual(cardOne, cardTwo) {
    return cardOne.objectId === cardTwo.objectId;
}

export default function ReturnCardsToLibraryChoice() {
    const gameState = useContext(GameState);
    const [cardsPutBack, setCardsPutBack] = useState([]);
    const cardsNotPutBack = _.filter(gameState.hand, card => !_.some(cardsPutBack, cardPutBack => areCardsEqual(card, cardPutBack)));

    const enoughCardsPutBack = (cardsPutBack.length === gameState.currentChoice.details.numberOfCardsToReturn);

    function putCardBack(card) {
        setCardsPutBack([...cardsPutBack, card]);
    }
    function undoPutBack(card) {
        setCardsPutBack(_.filter(cardsPutBack, cardPutBack => !areCardsEqual(card, cardPutBack)));
    }

    return <div>
        <div className="mb-3">
            <HorizontalCenter>
                {_.map(_.reverse(cardsPutBack), card => <Card key={card.objectId} card={card} onClick={() => undoPutBack(card)} className="cardOverlap" />)}
                {_.fill(Array(5), <CardBack className="cardOverlap"/>)}
            </HorizontalCenter>
        </div>
        <HorizontalCenter>
            {_.map(cardsNotPutBack, card => <Card key={card.objectId} card={card} onClick={!enoughCardsPutBack && (() => putCardBack(card))} className="mx-1" />)}
        </HorizontalCenter>
        <HorizontalCenter className="mt-2">
            <DecisionButton optionToChoose={_.map(cardsPutBack, card => card.objectId).join(" ")} text="Confirm" disabled={!enoughCardsPutBack}/>
        </HorizontalCenter>
    </div>;
}
