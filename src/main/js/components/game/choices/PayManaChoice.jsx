import {useContext} from "preact/hooks";
import GameState from "../../../contexts/GameState";
import BannerText from "../../layout/BannerText";
import HorizontalCenter from "../../layout/HorizontalCenter";
import {useActionMenu} from "../actions/ActionMenu";
import ManaCost from "../card/ManaCost";
import UndoButton from "../UndoButton";

export default function PayManaChoice() {
    const gameState = useContext(GameState);
    const choice = gameState.currentChoice.details;
    const actionMenu = useActionMenu(gameState.currentChoice.details.availableManaAbilities);
    return <div>
        <BannerText as="p">Pay <ManaCost manaCost={choice.remainingCost} /> </BannerText>
        <HorizontalCenter>
            <UndoButton />
        </HorizontalCenter>
        {actionMenu}
    </div>;
}
