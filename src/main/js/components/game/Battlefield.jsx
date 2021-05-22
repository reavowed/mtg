import {useContext} from "preact/hooks";
import GameState from "../../GameState";
import _ from "lodash";
import CardRow from "./CardRow";

export default function Battlefield() {
    const gameState = useContext(GameState);
    const player = gameState.player;
    const opponent = _.find(gameState.gameData.playersInTurnOrder, p => p !== player);
    return <div className="h-100">
        <div className="h-50 border-bottom d-flex flex-column justify-content-start">
            <CardRow cards={gameState.battlefield[opponent]} className="my-2"/>
        </div>
        <div className="h-50 d-flex flex-column justify-content-end">
            <CardRow cards={gameState.battlefield[player]} className="my-2"/>
        </div>
    </div>;
}
