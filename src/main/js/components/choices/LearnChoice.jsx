import {useContext} from "preact/hooks";
import GameState from "../../contexts/GameState";
import CardRow from "../card/CardRow";
import CardWithText from "../card/CardWithText";
import BannerText from "../layoutUtils/BannerText";
import PopupChoice from "./PopupChoice";
import distinct from "lodash/uniq"
import find from "lodash/find"

export default function LearnChoice() {
    const gameState = useContext(GameState);
    const validLessonIds = distinct(gameState.currentChoice.details.possibleLessons);
    const getId = ({artDetails: {set, collectorNumber}}) => set + "-" + collectorNumber;
    const validLessons = validLessonIds.map(id => find(gameState.sideboard, card => getId(card) === id));

    return <PopupChoice text="Learn">
        <BannerText>Learn</BannerText>
        <CardRow cards={validLessons} as={CardWithText} />
    </PopupChoice>;
}
