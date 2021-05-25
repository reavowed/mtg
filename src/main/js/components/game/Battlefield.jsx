import _ from "lodash";
import {Fragment} from "preact";
import {useContext} from "preact/hooks";
import {OverlayTrigger, Popover} from "react-bootstrap";
import GameState from "../../contexts/GameState";
import StopsManager from "../../contexts/StopsManager";
import CardRow from "./CardRow";

function StopForStepOrPhaseWithoutSteps({player, phaseOrStep, name}) {
    const stopsManager = useContext(StopsManager);
    const isSet = stopsManager.isStopSet(player, phaseOrStep);
    const className = "btn " + (isSet ? "btn-primary" : "btn-outline-primary") + " ml-2";
    return <button className={className} onClick={() => stopsManager.setStop(player, phaseOrStep, !isSet)}>{name}</button>;
}

function StopForPhaseWithSteps({player, name, steps, direction}) {
    const stopsManager = useContext(StopsManager);
    const stepsAreSet = _.map(steps, ({step}) => stopsManager.isStopSet(player, step));
    const allStepsSet = _.every(stepsAreSet);
    const someStepsSet = _.some(stepsAreSet);
    const className = "btn " + (someStepsSet ? "btn-primary" : "btn-outline-primary") + " ml-2";
    const style = (someStepsSet && !allStepsSet) ? {opacity: 0.5} : {};

    const popover = <Popover id={name}>
        <Popover.Content>
            {_.map(steps, ({step, name}) => <StopForStepOrPhaseWithoutSteps key={step} player={player} phaseOrStep={step} name={name} />)}
        </Popover.Content>
    </Popover>

    return <OverlayTrigger trigger="click" rootClose={true} placement={direction} overlay={popover}>
        <button className={className} style={style}>{name}</button>
    </OverlayTrigger>
}

function Stops({player, direction}) {
    const beginningSteps = [
        {
            step: "UpkeepStep",
            name: "Upkeep"
        },
        {
            step: "DrawStep",
            name: "Draw"
        }
    ];
    const combatSteps = [
        {
            step: "BeginningOfCombatStep",
            name: "Beginning"
        },
        {
            step: "DeclareAttackersStep",
            name: "Attackers"
        },
        {
            step: "DeclareBlockersStep",
            name: "Blockers"
        },
        {
            step: "CombatDamageStep",
            name: "Damage"
        },
        {
            step: "EndOfCombatStep",
            name: "End"
        }
    ];
    return <Fragment>
        <StopForPhaseWithSteps player={player} name="Beginning" steps={beginningSteps} direction={direction} />
        <StopForStepOrPhaseWithoutSteps player={player} phaseOrStep="PrecombatMainPhase" name="Precombat" />
        <StopForPhaseWithSteps player={player} name="Combat" steps={combatSteps} direction={direction} />
        <StopForStepOrPhaseWithoutSteps player={player} phaseOrStep="PostcombatMainPhase" name="Postcombat" />
        <StopForStepOrPhaseWithoutSteps player={player} phaseOrStep="EndStep" name="End" />
    </Fragment>;
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
                <div className="mb-2"><Stops player={opponent} direction="top" /></div>
            </div>
            <CardRow cards={opponentNoncreatures} className="my-2"/>
            <CardRow cards={opponentCreatures} className="my-2"/>
        </div>
        <div className="h-50 d-flex flex-column justify-content-end position-relative">
            <div class="position-absolute position-top d-flex align-items-start">
                <h4 class="m-0 p-2 border-bottom border-right">{gameState.lifeTotals[player]}</h4>
                <div className="mt-2"><Stops player={player} direction="bottom"/></div>
            </div>
            <CardRow cards={playerCreatures} className="my-2"/>
            <CardRow cards={playerNoncreatures} className="my-2"/>
        </div>
    </div>;
}
