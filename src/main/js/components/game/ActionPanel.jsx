import {Fragment} from "preact";
import {useContext} from "preact/hooks";
import GameState from "../../GameState";
import BannerText from "../layout/BannerText";
import PriorityChoice from "./choices/PriorityChoice";

function getChoiceDisplay(choiceType) {
    switch (choiceType) {
        case "PriorityChoice":
            return <PriorityChoice />
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
        <BannerText as="h4">{getTurnDescription(gameState)}</BannerText>
        {contents}
    </div>;
}
