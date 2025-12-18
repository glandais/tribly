import { useParams, Navigate } from 'react-router-dom'

export function TeamDetailPage() {
  const { teamSlug } = useParams<{ teamSlug: string }>()

  // Redirect to rides tab as default (for both members and non-members)
  return <Navigate to={`/teams/${teamSlug}/rides`} replace />
}
