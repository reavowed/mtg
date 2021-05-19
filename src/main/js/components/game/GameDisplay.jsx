import {useContext} from "preact/hooks";
import GameState from "../../GameState";
import ActiveGameController from "./ActiveGameController";
import EventLog from "./EventLog";
import OpeningHand from "./openingHand/OpeningHand";

function MainDisplay() {
    const gameState = useContext(GameState);
    if (gameState.currentTurnNumber === 0) {
        return <OpeningHand />;
    } else {
        return <ActiveGameController />;
    }
}

export default function GameDisplay() {
    return <div class="d-flex">
        <div class="w-75">
            <MainDisplay />
        </div>
        <div class="w-25">
            <EventLog />
        </div>
    </div>;
}
