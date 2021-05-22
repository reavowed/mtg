import {useContext} from "preact/hooks";
import GameState from "../../GameState";
import _ from "lodash";
import CardRow from "./CardRow";

export default function Battlefield() {
    const gameState = useContext(GameState);
    const player = gameState.player;
    const opponent = _.find(gameState.gameData.playersInTurnOrder, p => p !== player);
    const opponentCreatures = _.filter(gameState.battlefield[opponent], object => _.includes(object.characteristics.types, "Creature"));
    const opponentNoncreatures = _.filter(gameState.battlefield[opponent], object => !_.includes(object.characteristics.types, "Creature"));
    const playerCreatures = _.filter(gameState.battlefield[player], object => _.includes(object.characteristics.types, "Creature"));
    const playerNoncreatures = _.filter(gameState.battlefield[player], object => !_.includes(object.characteristics.types, "Creature"));
    return <div className="h-100">
        <div className="h-50 border-bottom d-flex flex-column justify-content-start">
            <CardRow cards={opponentNoncreatures} className="my-2"/>
            <CardRow cards={opponentCreatures} className="my-2"/>
        </div>
        <div className="h-50 d-flex flex-column justify-content-end">
            <CardRow cards={playerCreatures} className="my-2"/>
            <CardRow cards={playerNoncreatures} className="my-2"/>
        </div>
    </div>;
}
