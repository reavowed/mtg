import {useContext} from "preact/hooks";
import GameState from "../../GameState";
import CardColumn from "./CardColumn";

export default function Stack() {
    const gameState = useContext(GameState);
    if (gameState.stack.length > 0) return <div className="h-100 border-left">
        <CardColumn cards={gameState.stack} />
    </div>
}
