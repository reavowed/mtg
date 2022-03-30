import {useContext} from "preact/hooks";
import GameState from "../../contexts/GameState";
import {addClass} from "../../utils/element-utils";
import _ from "lodash";
import CardImage from "./card/CardImage";

export default function Exile({player, className, ...props}) {
    const gameState = useContext(GameState);
    const exile = gameState.exile[player]
    const exileSize = _.isNumber(exile) ? exile : exile.length;
    const topCard = exileSize > 0 && exile[exileSize - 1];
    return exileSize > 0 ?
        <div className={addClass(className, "zoneWithCount exile")} {...props}>
            <CardImage card={topCard} key={topCard.objectId} />
            <span className="zoneCount">{exileSize}</span>
        </div> :
        <div className={addClass(className, "exileOutline")} {...props}/>;
}
