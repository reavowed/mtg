import {useContext} from "preact/hooks";
import GameState from "../../GameState";
import ScreenCenter from "../layout/ScreenCenter";
import ActionPanel from "./ActionPanel";
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
    return <div className="d-flex">
        <div className="w-75">
            <MainDisplay />
        </div>
        <div className="w-25 border-left">
            <div className="d-flex flex-column w-100 vh-100">
                <div className="flex-grow-1">
                    <EventLog />
                </div>
                <div style={{height: 200}} className="border-top">
                    <ScreenCenter>
                        <ActionPanel />
                    </ScreenCenter>
                </div>
            </div>
        </div>
    </div>;
}
