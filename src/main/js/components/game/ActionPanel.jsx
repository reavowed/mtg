import {useContext} from "preact/hooks";
import GameState from "../../contexts/GameState";
import BannerText from "../layout/BannerText";
import DeclareAttackers from "./choices/DeclareAttackers";
import PriorityChoice from "./choices/PriorityChoice";
import ManaPool from "./ManaPool";

function getChoiceDisplay(choiceType) {
    switch (choiceType) {
        case "PriorityChoice":
            return <PriorityChoice />
        case "ChooseAttackers":
            return <DeclareAttackers />
    }
}

function convertPhaseOrStepToDisplayText(name) {
    return name.split(/(?=[A-Z])/).join(" ");
}

function getTurnDescription(gameState) {
    return "Turn " + gameState.currentTurnNumber + " (" + gameState.player + ") - " + convertPhaseOrStepToDisplayText(gameState.currentStep || gameState.currentPhase);
}

export default function ActionPanel() {
    const gameState = useContext(GameState);
    const contents = (gameState.currentChoice.playerToAct !== gameState.player) ?
        <BannerText as="p">Waiting for opponent</BannerText> :
        getChoiceDisplay(gameState.currentChoice.type);
    return <div>
        <ManaPool />
        <BannerText as="h4">{getTurnDescription(gameState)}</BannerText>
        {contents}
    </div>;
}
