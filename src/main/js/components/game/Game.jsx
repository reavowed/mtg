import {useEffect, useState} from "preact/hooks";
import DecisionMaker from "../../DecisionMaker";
import GameState from "../../GameState";
import HorizontalCenter from "../layout/HorizontalCenter";
import {getJson} from "../../utils/fetch-helpers";
import OpeningHand from "./openingHand/OpeningHand";

export default function Game() {
    const [gameState, setGameState] = useState(null);
    useEffect(() => {
        getJson('$currentPath/state').then(setGameState)
    }, []);

    const content = gameState ?
        <OpeningHand /> :
        <div className="spinner-border"/>;
    return <GameState.Provider value={gameState}>
        <DecisionMaker.Provider>
            <div className="d-flex flex-column justify-content-center vh-100">
                <HorizontalCenter>
                    {content}
                </HorizontalCenter>
            </div>
        </DecisionMaker.Provider>
    </GameState.Provider>;
}
