import {useContext} from "preact/hooks";
import GameState from "../../contexts/GameState";
import BannerText from "../layout/BannerText";
import AssignCombatDamageChoice from "./choices/AssignCombatDamageChoice";
import DeclareAttackersChoice from "./choices/DeclareAttackersChoice";
import DeclareBlockersChoice from "./choices/DeclareBlockersChoice";
import OrderBlockersChoice from "./choices/OrderBlockersChoice";
import PriorityChoice from "./choices/PriorityChoice";
import ScryChoice from "./choices/ScryChoice";
import SearchChoice from "./choices/SearchChoice";
import TargetChoice from "./choices/TargetChoice";
import TriggeredAbilityChoice from "./choices/TriggeredAbilityChoice";
import ManaPool from "./ManaPool";
import UndoButton from "./UndoButton";
import ModeChoice from "./choices/ModeChoice";

function getChoiceDisplay(choiceType) {
    switch (choiceType) {
        case "PriorityChoice":
            return <PriorityChoice />
        case "DeclareAttackersChoice":
            return <DeclareAttackersChoice />
        case "DeclareBlockersChoice":
            return <DeclareBlockersChoice />
        case "OrderBlockersChoice":
            return <OrderBlockersChoice />
        case "AssignCombatDamageChoice":
            return <AssignCombatDamageChoice />
        case "SearchChoice":
            return <SearchChoice />
        case "TargetChoice":
            return <TargetChoice />
        case "ScryChoice":
            return <ScryChoice />
        case "TriggeredAbilityChoice":
            return <TriggeredAbilityChoice />
        case "ModeChoice":
            return <ModeChoice />
    }
}

function convertPhaseOrStepToDisplayText(name) {
    return name.split(/(?=[A-Z])/).join(" ");
}

function getTurnDescription(gameState) {
    return gameState.currentTurnNumber > 0 && "Turn " + gameState.currentTurnNumber + " (" + gameState.player + ") - " + convertPhaseOrStepToDisplayText(gameState.currentStep || gameState.currentPhase);
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
