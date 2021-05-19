export default function VerticalCenter({children}) {
    return <div className="d-flex flex-column justify-content-center h-100">
        {children}
    </div>
}
