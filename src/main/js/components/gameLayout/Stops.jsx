import _ from "lodash";
import {Fragment} from "preact";
import {useContext} from "preact/hooks";
import {OverlayTrigger, Popover} from "react-bootstrap";
import StopsManager from "../../contexts/StopsManager";

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

export default function Stops({player, direction}) {
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
