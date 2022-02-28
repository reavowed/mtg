import _ from "lodash";
import {useCallback, useContext} from "preact/hooks";
import ActionManager from "../../contexts/ActionManager";
import GameState from "../../contexts/GameState";
import HorizontalCenter from "../layout/HorizontalCenter";

export default function ManaPool() {
    const gameState = useContext(GameState);
    const actionManager = useContext(ActionManager);

    const manaPool = gameState.manaPools[gameState.player];
    const onClick = useCallback((manaObject) => {
        actionManager.manaActionHandler(manaObject.id);
    }, [actionManager]);

    return <HorizontalCenter>
        {_.map(manaPool, manaObject => <span className={"card-symbol card-symbol-" + manaObject.manaType} onClick={() => onClick(manaObject)}/>)}
    </HorizontalCenter>;
}
