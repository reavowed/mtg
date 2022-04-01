import {useContext} from "preact/hooks";
import GameState from "../../contexts/GameState";
import {addClass} from "../../utils/element-utils";
import CardBack from "../card/CardBack";

export default function Library({player, className, ...props}) {
    const gameState = useContext(GameState);
    const library = gameState.libraries[player]
    return <div className="zoneContainer">
        <div className="zoneLabel">Library</div>
        <div className={addClass(className, "zoneWithCount library")} {...props}>
            <CardBack />
            <span className="zoneCount">{library.length}</span>
        </div>
    </div>;
}
