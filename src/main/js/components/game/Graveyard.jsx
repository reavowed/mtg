import {useContext} from "preact/hooks";
import GameState from "../../contexts/GameState";
import {addClass} from "../../utils/element-utils";
import _ from "lodash";
import CardImage from "./card/CardImage";

export default function Graveyard({player, className, ...props}) {
    const gameState = useContext(GameState);
    const graveyard = gameState.graveyards[player]
    const graveyardSize = _.isNumber(graveyard) ? graveyard : graveyard.length;
    const topCard = graveyardSize > 0 && graveyard[graveyardSize - 1];
    return graveyardSize > 0 ?
        <div className={addClass(className, "zoneWithCount graveyard")} {...props}>
            <CardImage card={topCard} key={topCard.objectId} />
            <span className="zoneCount">{graveyardSize}</span>
        </div> :
        <div className={addClass(className, "graveyardOutline")} {...props}/>;
}
