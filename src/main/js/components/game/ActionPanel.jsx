import {useContext} from "preact/hooks";
import GameState from "../../contexts/GameState";
import BannerText from "../layout/BannerText";
import AssignCombatDamageChoice from "./choices/AssignCombatDamageChoice";
import DeclareAttackersChoice from "./choices/DeclareAttackersChoice";
import DeclareBlockersChoice from "./choices/DeclareBlockersChoice";
import ModeChoice from "./choices/ModeChoice";
import OrderBlockersChoice from "./choices/OrderBlockersChoice";
import PayManaChoice from "./choices/PayManaChoice";
import PriorityChoice from "./choices/PriorityChoice";
import ScryChoice from "./choices/ScryChoice";
import SearchLibraryChoice from "./choices/SearchLibraryChoice";
import TargetChoice from "./choices/TargetChoice";
import TriggeredAbilityChoice from "./choices/TriggeredAbilityChoice";
import ManaPool from "./ManaPool";

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
        case "SearchLibraryChoice":
            return <SearchLibraryChoice />
        case "TargetChoice":
            return <TargetChoice />
        case "ScryChoice":
            return <ScryChoice />
        case "TriggeredAbilityChoice":
            return <TriggeredAbilityChoice />
        case "ModeChoice":
            return <ModeChoice />
        case "PayManaChoice":
            return <PayManaChoice />
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
