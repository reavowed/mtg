import {useContext} from "preact/hooks";
import GameState from "../../contexts/GameState";
import ScreenCenter from "../layoutUtils/ScreenCenter";
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
                <div className="flex-grow-1 flex-shrink-1" style={{overflowY: "scroll"}}>
                    <EventLog/>
                </div>
                <div style={{height: 200}} className="border-top flex-grow-0 flex-shrink-0">
                    <ScreenCenter>
                        <ActionPanel/>
                    </ScreenCenter>
                </div>
            </div>
        </div>
    </div>;
}
