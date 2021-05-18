import _ from "lodash";
import {useCallback, useContext, useState} from "preact/hooks";
import GameState from "../../../GameState";
import {getPlural} from "../../../utils/word-helpers";
import BannerText from "../../layout/BannerText";
import HorizontalCenter from "../../layout/HorizontalCenter";
import Card from "../Card";
import CardBack from "../CardBack";
import DecisionButton from "../DecisionButton";
import Hand from "../Hand";

function areCardsEqual(cardOne, cardTwo) {
    return cardOne.objectId === cardTwo.objectId;
}

export default function ReturnCardsToLibraryChoice() {
    const gameState = useContext(GameState);
    const [cardsPutBack, setCardsPutBack] = useState([]);
    const cardsNotPutBack = _.filter(gameState.hand, card => !_.some(cardsPutBack, cardPutBack => areCardsEqual(card, cardPutBack)));
    const numberOfCardsToReturn = gameState.currentChoice.details.numberOfCardsToReturn;
    const enoughCardsPutBack = (cardsPutBack.length === numberOfCardsToReturn);

    const putCardBack = useCallback(
        (card) => setCardsPutBack([...cardsPutBack, card]),
        [cardsPutBack]);
    const undoPutBack = useCallback(
        (card) => setCardsPutBack(_.filter(cardsPutBack, cardPutBack => !areCardsEqual(card, cardPutBack))),
        [cardsPutBack]);

    const CardToPutBack = useCallback(
        ({card, ...props}) => <Card card={card} onClick={!enoughCardsPutBack && (() => putCardBack(card))} {...props}/>,
        [cardsPutBack]);

    return <div>
        <div className="mb-3">
            <HorizontalCenter>
                {_.map(_.reverse([...cardsPutBack]), card => <Card key={card.objectId} card={card} onClick={() => undoPutBack(card)} className="cardOverlap" />)}
                {_.fill(Array(5), <CardBack className="cardOverlap"/>)}
            </HorizontalCenter>
        </div>
        <HorizontalCenter>
            <Hand cards={cardsNotPutBack} as={CardToPutBack}/>
        </HorizontalCenter>
        <BannerText>Choose {getPlural(numberOfCardsToReturn, "card", "cards")} to put back</BannerText>
        <HorizontalCenter className="mt-2">
            <DecisionButton optionToChoose={_.map(cardsPutBack, card => card.objectId).join(" ")} text="Confirm" disabled={!enoughCardsPutBack}/>
        </HorizontalCenter>
    </div>;
}
