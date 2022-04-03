import {useContext, useState} from "preact/hooks";
import {Button} from "react-bootstrap";
import GameState from "../../contexts/GameState";
import CardRow from "../card/CardRow";
import CardWithText from "../card/CardWithText";
import DecisionButton from "../DecisionButton";
import BannerText from "../layoutUtils/BannerText";
import HorizontalCenter from "../layoutUtils/HorizontalCenter";
import PopupChoice from "./PopupChoice";
import distinct from "lodash/uniq"
import find from "lodash/find"

export default function LearnChoice() {
    const gameState = useContext(GameState);

    const sideboard = gameState.sideboards[gameState.player];
    const hand = gameState.hands[gameState.player];
    const validLessons = gameState.currentChoice.details.possibleLessons.map(id => find(sideboard, card => card.objectId === id));

    const canGetLesson = (validLessons.length > 0);
    const canDiscard = (hand.length > 0);

    const [decision, setDecision] = useState(null);
    const [actionType, setActionType] = useState(canGetLesson ? "lesson" : canDiscard ? "discard" : "decline");

    function ChoosableCard({card, ...props}) {
        const isChosen = card.objectId === decision;
        const className = isChosen ? "selected" : "selectable";
        const onClick = () => setDecision(card.objectId);
        return <CardWithText card={card} onClick={onClick} className={className} {...props} />
    }

    function ActionTypeButton({children, stateValue, defaultDecision = null, ...props}) {
        if (actionType === stateValue) {
            props.variant = "success";
        }
        function onClick() {
            setActionType(stateValue);
            setDecision(defaultDecision);
        }
        return <Button variant='primary' size='lg' onClick={onClick} {...props}>{children}</Button>
    }


    return <PopupChoice text="Learn">
        <BannerText>Learn</BannerText>
        <CardRow cards={actionType === "lesson" ? validLessons : actionType === "discard" ? hand : []} as={ChoosableCard} className="mt-4" />
        <HorizontalCenter className="mt-4">
            <ActionTypeButton stateValue="lesson" disabled={!canGetLesson}>Lesson</ActionTypeButton>
            <ActionTypeButton stateValue="discard" className="ml-4" disabled={!canDiscard}>Discard</ActionTypeButton>
            <ActionTypeButton stateValue="decline" className="ml-4" defaultDecision="Decline">Decline</ActionTypeButton>
        </HorizontalCenter>
        <HorizontalCenter className="mt-4">
            <DecisionButton optionToChoose={decision} disabled={!decision}>Submit</DecisionButton>
        </HorizontalCenter>
    </PopupChoice>;
}
