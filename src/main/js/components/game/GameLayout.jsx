import {useContext} from "preact/hooks";
import GameState from "../../GameState";
import ScreenCenter from "../layout/ScreenCenter";
import ActionPanel from "./ActionPanel";
import ZoneLayout from "./ZoneLayout";
import EventLog from "./EventLog";
import OpeningHand from "./openingHand/OpeningHand";

export default function GameLayout() {
    const gameState = useContext(GameState);
    return <div className="d-flex">
        <div className="w-75">
            {gameState.currentTurnNumber == 0 ? <OpeningHand/> : <ZoneLayout/>}
        </div>
        <div className="w-25 border-left">
            <div className="d-flex flex-column w-100 vh-100">
                <div className="flex-grow-1">
                    <EventLog/>
                </div>
                <div style={{height: 200}} className="border-top">
                    <ScreenCenter>
                        <ActionPanel/>
                    </ScreenCenter>
                </div>
            </div>
        </div>
    </div>;
}