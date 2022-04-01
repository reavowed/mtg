import {useContext} from "preact/hooks";
import GameState from "../../contexts/GameState";
import CardRow from "../card/CardRow";
import CardWithText from "../card/CardWithText";
import BannerText from "../layoutUtils/BannerText";
import PopupChoice from "./PopupChoice";

export default function LearnChoice() {
    const gameState = useContext(GameState);
    return <PopupChoice text="Learn">
        <BannerText>Learn</BannerText>
        <CardRow cards={gameState.sideboard} as={CardWithText} />
    </PopupChoice>;
}
