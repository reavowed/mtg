import {useContext, useState} from "preact/hooks";
import GameState from "../../contexts/GameState";
import CardRow from "../card/CardRow";
import CardWithText from "../card/CardWithText";
import BannerText from "../layoutUtils/BannerText";
import PopupChoice from "./PopupChoice";
import distinct from "lodash/uniq"
import find from "lodash/find"

export default function LearnChoice() {
    const gameState = useContext(GameState);
    const [chosenLesson, setChosenLesson] = useState(null);
    const getId = ({artDetails: {set, collectorNumber}}) => set + "-" + collectorNumber;

    function ChoosableLesson({card, ...props}) {
        const id = getId(card);
        const isChosen = id === chosenLesson;
        const className = isChosen ? "selected" : "selectable";
        const onClick = () => setChosenLesson(id);
        return <CardWithText card={card} onClick={onClick} className={className} {...props} />
    }

    const validLessonIds = distinct(gameState.currentChoice.details.possibleLessons);
    const validLessons = validLessonIds.map(id => find(gameState.sideboard, card => getId(card) === id));

    return <PopupChoice text="Learn">
        <BannerText>Learn</BannerText>
        <CardRow cards={validLessons} as={ChoosableLesson} />
    </PopupChoice>;
}
