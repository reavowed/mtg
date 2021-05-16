import DecisionMaker from "../../DecisionMaker";
import GameState from "../../GameState";
import OpeningHand from "./openingHand/OpeningHand";

export default function Game() {
    return <GameState.Provider>
        <DecisionMaker.Provider>
            <OpeningHand />
        </DecisionMaker.Provider>
    </GameState.Provider>;
}
