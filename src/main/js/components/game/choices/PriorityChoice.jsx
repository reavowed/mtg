import {useContext} from "preact/hooks";
import GameState from "../../../contexts/GameState";
import BannerText from "../../layout/BannerText";
import HorizontalCenter from "../../layout/HorizontalCenter";
import {useActionMenu} from "../actions/ActionMenu";
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
