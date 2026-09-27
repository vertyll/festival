export default function StatTile({
  header,
  value,
  description,
}: Readonly<{ header: string; value: string; description: string }>) {
  return (
    <div className="tile">
      <h3 className="tile-header">{header}</h3>
      <div className="tile-number">{value}</div>
      <div className="tile-desc">{description}</div>
    </div>
  );
}
