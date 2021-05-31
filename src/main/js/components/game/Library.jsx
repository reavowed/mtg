import {useContext} from "preact/hooks";
import GameState from "../../contexts/GameState";
import {addClass} from "../../utils/element-utils";
import CardBack from "./card/CardBack";
import _ from "lodash";

export default function Library({player, className, ...props}) {
    const gameState = useContext(GameState);
    const library = gameState.libraries[player]
    const librarySize = _.isNumber(library) ? library : library.length;
    return <div className={addClass(className, "library")} {...props}>
        <CardBack />
        <span className="zoneCount">{librarySize}</span>
    </div>
}
