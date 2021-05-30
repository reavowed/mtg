import _ from "lodash";
import {Fragment} from "preact";
import {useContext, useEffect} from "preact/hooks";
import {OverlayTrigger, Popover} from "react-bootstrap";
import ActionManager from "../../contexts/ActionManager";
import CanvasManager from "../../contexts/CanvasManager";
import GameState from "../../contexts/GameState";
import StopsManager from "../../contexts/StopsManager";
import {addClass} from "../../utils/element-utils";
import CardRow from "./card/CardRow";

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

function PlayerLifeTotal({player, direction, ...props}) {
    const gameState = useContext(GameState);
    const actionManager = useContext(ActionManager);
    const className = `border-${direction} border-right ` + actionManager.getClasses(player).map(c => c).join(" ");
    return <div className={className} {...props} onClick={event => actionManager.actionHandler(player, event)}>
        <h4 className="m-0 p-2">{gameState.lifeTotals[player]}</h4>
    </div>
}

export default function Battlefield() {
    const gameState = useContext(GameState);
    const canvasManager = useContext(CanvasManager);

    const player = gameState.player;
    const opponent = _.find(gameState.gameData.playersInTurnOrder, p => p !== player);
    const opponentCreatures = _.filter(gameState.battlefield[opponent], object => _.includes(object.characteristics.types, "Creature"));
    const opponentNoncreatures = _.filter(gameState.battlefield[opponent], object => !_.includes(object.characteristics.types, "Creature"));
    const playerCreatures = _.filter(gameState.battlefield[player], object => _.includes(object.characteristics.types, "Creature"));
    const playerNoncreatures = _.filter(gameState.battlefield[player], object => !_.includes(object.characteristics.types, "Creature"));

    // Show blocker lines
    useEffect(() => {
        const lines = _.flatMap([...playerCreatures, ...opponentCreatures], creature => {
            if (creature.modifiers.blocking) {
                return [[creature.objectId, creature.modifiers.blocking]];
            } else {
                return [];
            }
        });
        canvasManager.setLines(lines);
        return () => canvasManager.setLines([]);
    }, [gameState]);

    return <div className="h-100">
        <div className="h-50 border-bottom d-flex flex-column justify-content-start position-relative">
            <div class="position-absolute position-bottom d-flex align-items-end">
                <PlayerLifeTotal direction="top" player={opponent}/>
                <div className="mb-2"><Stops player={opponent} direction="top" /></div>
            </div>
            <CardRow cards={opponentNoncreatures} className="my-2"/>
            <CardRow cards={opponentCreatures} className="my-2"/>
        </div>
        <div className="h-50 d-flex flex-column justify-content-end position-relative">
            <div class="position-absolute position-top d-flex align-items-start">
                <PlayerLifeTotal direction="bottom" player={player}/>
                <div className="mt-2"><Stops player={player} direction="bottom"/></div>
            </div>
            <CardRow cards={playerCreatures} className="my-2"/>
            <CardRow cards={playerNoncreatures} className="my-2"/>
        </div>
    </div>;
}
