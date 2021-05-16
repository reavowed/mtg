export default function VerticalCenter({children}) {
    return <div className="d-flex flex-column justify-content-center vh-100">
        {children}
    </div>
}
