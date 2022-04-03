import {useContext, useState} from "preact/hooks";
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
    const [chosenLesson, setChosenLesson] = useState(null);

    const sideboard = gameState.sideboards[gameState.player];

    function ChoosableLesson({card, ...props}) {
        const isChosen = card.objectId === chosenLesson;
        const className = isChosen ? "selected" : "selectable";
        const onClick = () => setChosenLesson(card.objectId);
        return <CardWithText card={card} onClick={onClick} className={className} {...props} />
    }

    const validLessons = gameState.currentChoice.details.possibleLessons.map(id => find(sideboard, card => card.objectId === id));

    return <PopupChoice text="Learn">
        <BannerText>Learn</BannerText>
        <CardRow cards={validLessons} as={ChoosableLesson} />
        <HorizontalCenter className="mt-4">
            <DecisionButton optionToChoose={chosenLesson} disabled={!chosenLesson}>Submit</DecisionButton>
        </HorizontalCenter>
    </PopupChoice>;
}
