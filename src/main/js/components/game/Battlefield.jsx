import _ from "lodash";
import {useContext} from "preact/hooks";
import GameState from "../../contexts/GameState";
import StopsManager from "../../contexts/StopsManager";
import CardRow from "./CardRow";

function Stops({player}) {
    const stopsManager = useContext(StopsManager);

    function Stop({phaseOrStep, text}) {
        return <span className="ml-2">
            <input type="checkbox" className="mr-1" checked={stopsManager.isStopSet(player, phaseOrStep)} onChange={ e => stopsManager.setStop(player, phaseOrStep, e.target.checked)}/>
            {text}
        </span>
    }
    const phases = [
        {
            phaseOrStep: "UpkeepStep",
            text: "Upkeep"
        },
        {
            phaseOrStep: "DrawStep",
            text: "Draw"
        },
        {
            phaseOrStep: "PrecombatMainPhase",
            text: "Precombat Main"
        },
        {
            phaseOrStep: "PostcombatMainPhase",
            text: "Postcombat Main"
        }
    ]
    return _.map(phases, phase => <Stop key={phase.phaseOrStep} phaseOrStep={phase.phaseOrStep} text={phase.text}/>);
}

export default function Battlefield() {
    const gameState = useContext(GameState);
    const player = gameState.player;
    const opponent = _.find(gameState.gameData.playersInTurnOrder, p => p !== player);
    const opponentCreatures = _.filter(gameState.battlefield[opponent], object => _.includes(object.characteristics.types, "Creature"));
    const opponentNoncreatures = _.filter(gameState.battlefield[opponent], object => !_.includes(object.characteristics.types, "Creature"));
    const playerCreatures = _.filter(gameState.battlefield[player], object => _.includes(object.characteristics.types, "Creature"));
    const playerNoncreatures = _.filter(gameState.battlefield[player], object => !_.includes(object.characteristics.types, "Creature"));
    return <div className="h-100">
        <div className="h-50 border-bottom d-flex flex-column justify-content-start position-relative">
            <div class="position-absolute position-bottom d-flex align-items-end">
                <h4 class="m-0 p-2 border-top border-right">{gameState.lifeTotals[opponent]}</h4>
                <div className="mb-2"><Stops player={opponent}/></div>
            </div>
            <CardRow cards={opponentNoncreatures} className="my-2"/>
            <CardRow cards={opponentCreatures} className="my-2"/>
        </div>
        <div className="h-50 d-flex flex-column justify-content-end position-relative">
            <div class="position-absolute position-top d-flex align-items-start">
                <h4 class="m-0 p-2 border-bottom border-right">{gameState.lifeTotals[player]}</h4>
                <div className="mt-2"><Stops player={player}/></div>
            </div>
            <CardRow cards={playerCreatures} className="my-2"/>
            <CardRow cards={playerNoncreatures} className="my-2"/>
        </div>
    </div>;
}
