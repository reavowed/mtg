import _ from "lodash";
import {useContext} from "preact/hooks";
import GameState from "../../contexts/GameState";
import HorizontalCenter from "../layout/HorizontalCenter";

export default function ManaPool() {
    const gameState = useContext(GameState);
    const manaTypes = gameState.manaPools[gameState.player];
    return <HorizontalCenter>
        {_.map(manaTypes, manaType => <span className={"card-symbol card-symbol-" + manaType}/>)}
    </HorizontalCenter>;
}
