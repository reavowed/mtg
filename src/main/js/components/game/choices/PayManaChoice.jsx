import {useCallback, useContext, useEffect} from "preact/hooks";
import ActionManager from "../../../contexts/ActionManager";
import DecisionMaker from "../../../contexts/DecisionMaker";
import GameState from "../../../contexts/GameState";
import BannerText from "../../layout/BannerText";
import HorizontalCenter from "../../layout/HorizontalCenter";
import {useActionMenu} from "../actions/ActionMenu";
import ManaCost from "../card/ManaCost";
import UndoButton from "../UndoButton";

export default function PayManaChoice() {
    const gameState = useContext(GameState);
    const actionManager = useContext(ActionManager);
    const decisionMaker = useContext(DecisionMaker);
    const choice = gameState.currentChoice.details;
    const actionMenu = useActionMenu(gameState.currentChoice.details.availableManaAbilities);
    const onManaObjectClick = useCallback((manaObjectId) => decisionMaker.makeDecision("Pay " + manaObjectId), [decisionMaker]);
    useEffect(() => {
        actionManager.setManaActionHandler(() => onManaObjectClick);
        return () => actionManager.setManaActionHandler(null);
    }, [onManaObjectClick]);
    return <div>
        <BannerText as="p">Pay <ManaCost manaCost={choice.remainingCost} /> </BannerText>
        <HorizontalCenter>
            <UndoButton />
        </HorizontalCenter>
        {actionMenu}
    </div>;
}
