import DecisionMaker from "../../DecisionMaker";
import GameState from "../../GameState";
import GameDisplay from "./GameDisplay";

export default function Game() {
    return <GameState.Provider>
        <DecisionMaker.Provider>
            <GameDisplay />
        </DecisionMaker.Provider>
    </GameState.Provider>;
}
