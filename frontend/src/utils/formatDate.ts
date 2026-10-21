export function formatDate(iso: string): string {
    const d = new Date(iso)
    const pad = (n: number) => String(n).padStart(2, '0')

    const hours = d.getHours()
    const ampm = hours >= 12 ? 'PM' : 'AM'
    const h12 = hours % 12 || 12

    return `${pad(d.getMonth() + 1)}-${pad(d.getDate())}-${d.getFullYear()} - ${pad(h12)}:${pad(d.getMinutes())}${ampm}`
}

