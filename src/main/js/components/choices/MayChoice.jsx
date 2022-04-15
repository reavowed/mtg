import {useContext} from "preact/hooks";
import GameState from "../../contexts/GameState";
import BannerText from "../layoutUtils/BannerText";
import HorizontalCenter from "../layoutUtils/HorizontalCenter";
import DecisionButton from "../DecisionButton";

export default function MayChoice() {
    const gameState = useContext(GameState);
    return <div>
        <BannerText as="p">Would you like to {gameState.currentChoice.details.text}?</BannerText>
        <HorizontalCenter>
            <DecisionButton optionToChoose="Yes">Yes</DecisionButton>
            <DecisionButton optionToChoose="No">No</DecisionButton>
        </HorizontalCenter>
    </div>

}
