import {useContext} from "preact/hooks";
import ActionManager from "../../contexts/ActionManager";
import GameState from "../../contexts/GameState";

export default function PlayerLifeTotal({player, direction, ...props}) {
    const gameState = useContext(GameState);
    const actionManager = useContext(ActionManager);
    const className = `player border-${direction} border-right ` + actionManager.getClasses(player).map(c => c).join(" ");
    return <div className={className} {...props} onClick={event => actionManager.actionHandler(player, event)}>
        <h4 className="m-0 p-2">{gameState.lifeTotals[player]}</h4>
    </div>
}
