import "bootstrap/js/dist/popover";
import find from "lodash/find";
import {useContext, useEffect, useState} from "preact/hooks";
import {Button} from "react-bootstrap";
import GameState from "../../contexts/GameState";
import CardRow from "../card/CardRow";
import CardWithText from "../card/CardWithText";
import DecisionButton from "../DecisionButton";
import BannerText from "../layoutUtils/BannerText";
import HorizontalCenter from "../layoutUtils/HorizontalCenter";
import PopupChoice from "./PopupChoice";
import $ from "jquery";

export default function LearnChoice() {
    const gameState = useContext(GameState);

    const sideboard = gameState.sideboards[gameState.player];
    const hand = gameState.hands[gameState.player];
    const validLessons = gameState.currentChoice.details.possibleLessons.map(id => find(sideboard, card => card.objectId === id));

    const canGetLesson = (validLessons.length > 0);
    const canDiscard = (hand.length > 0);

    const [decision, setDecision] = useState(null);
    const [actionType, setActionType] = useState(canGetLesson ? "lesson" : canDiscard ? "discard" : "decline");

    useEffect(() => {
        $('[data-toggle="popover"]').popover()
    });

    function ChoosableCard({card, ...props}) {
        const isChosen = card.objectId === decision;
        const className = isChosen ? "selected" : "selectable";
        const onClick = () => setDecision(card.objectId);
        return <CardWithText card={card} onClick={onClick} className={className} {...props} />
    }

    function ActionTypeButton({children, stateValue, popoverText, defaultDecision = null, ...props}) {
        if (actionType === stateValue) {
            props.variant = "success";
        }
        function onClick() {
            setActionType(stateValue);
            setDecision(defaultDecision);
        }
        const button = <Button variant='primary' size='lg' onClick={onClick} {...props}>{children}</Button>;
        if (popoverText) {
            return <span data-toggle="popover" data-trigger="hover" data-placement="auto" data-content={popoverText} data-container="body">{button}</span>;
        } else {
            return button;
        }
    }


    return <PopupChoice text="Learn">
        <BannerText>Learn</BannerText>
        <CardRow cards={actionType === "lesson" ? validLessons : actionType === "discard" ? hand : []} as={ChoosableCard} className="mt-4" />
        <HorizontalCenter className="mt-4">
            <ActionTypeButton stateValue="lesson" disabled={!canGetLesson} popoverText={!canGetLesson && "No lesson cards in sideboard to choose"}>Lesson</ActionTypeButton>
            <ActionTypeButton stateValue="discard" className="ml-4" disabled={!canDiscard} popoverText={!canDiscard && "No cards in hand to discard"}>Discard</ActionTypeButton>
            <ActionTypeButton stateValue="decline" className="ml-4" defaultDecision="Decline">Decline</ActionTypeButton>
        </HorizontalCenter>
        <HorizontalCenter className="mt-4">
            <DecisionButton optionToChoose={decision} disabled={!decision}>Submit</DecisionButton>
        </HorizontalCenter>
    </PopupChoice>;
}
