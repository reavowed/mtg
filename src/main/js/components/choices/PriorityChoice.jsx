import {useContext} from "preact/hooks";
import GameState from "../../contexts/GameState";
import BannerText from "../layoutUtils/BannerText";
import HorizontalCenter from "../layoutUtils/HorizontalCenter";
import {useActionMenu} from "../ActionMenu";
import DecisionButton from "../DecisionButton";
import UndoButton from "../UndoButton";

export default function PriorityChoice() {
    const gameState = useContext(GameState);
    const actionMenu = useActionMenu(gameState.currentChoice.details.availableActions);
    return <div>
        <BannerText as="p">Cast spells, activate abilities, or play a land.</BannerText>
        <HorizontalCenter>
            <DecisionButton optionToChoose="Pass">Pass</DecisionButton>
            <UndoButton/>
        </HorizontalCenter>
        {actionMenu}
    </div>;
}
