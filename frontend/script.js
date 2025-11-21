const API_BASE = 'http://localhost:8080';
const sampleJD = `주요업무\n- Java/Spring 기반 백엔드 신규 개발 및 성능 개선\n- AWS 환경에서 서비스 운영 경험 보유자 우대\n자격요건\n- OOP 기반 개발 경험\n- MySQL, Redis, Kafka 경험 우대`;

const page = document.body.dataset.page;
attachNavHandlers();

if (page === 'builder') {
    initBuilder();
} else if (page === 'my-roadmaps') {
    initDashboard();
} else if (page === 'community') {
    initCommunity();
}

function attachNavHandlers() {
    const loginBtn = document.getElementById('navLogin');
    const logoutBtn = document.getElementById('navLogout');
    if (loginBtn) loginBtn.onclick = fakeLogin;
    if (logoutBtn) logoutBtn.onclick = fakeLogout;
    updateAuthButtons();
}

function fakeLogin() {
    const email = prompt('이메일을 입력하세요', 'demo@user.com');
    const password = 'pass';
    fetch(`${API_BASE}/api/auth/login`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ email, password }) })
        .then(r => r.json())
        .then(data => { localStorage.setItem('token', data.token); updateAuthButtons(); alert('로그인 완료'); })
        .catch(() => alert('로그인 실패'));
}

function fakeLogout() {
    const token = localStorage.getItem('token');
    if (!token) return;
    fetch(`${API_BASE}/api/auth/logout`, { method: 'POST', headers: { 'X-Auth-Token': token } }).finally(() => {
        localStorage.removeItem('token');
        updateAuthButtons();
        alert('로그아웃되었습니다');
    });
}

function updateAuthButtons() {
    const token = localStorage.getItem('token');
    document.querySelectorAll('#navLogin').forEach(el => el.style.display = token ? 'none' : 'inline-flex');
    document.querySelectorAll('#navLogout').forEach(el => el.style.display = token ? 'inline-flex' : 'none');
}

// Builder page
function initBuilder() {
    const roadmapDiv = document.getElementById('roadmap');
    const postsDiv = document.getElementById('posts');
    const roadmapMeta = document.getElementById('roadmapMeta');
    const statusEl = document.getElementById('formStatus');

    document.getElementById('fillSample').addEventListener('click', () => {
        document.getElementById('jdText').value = sampleJD;
    });

    document.querySelectorAll('#jdTabs button').forEach(btn => {
        btn.addEventListener('click', () => {
            document.querySelectorAll('#jdTabs button').forEach(b => b.classList.remove('active'));
            document.querySelectorAll('.tab-content').forEach(c => c.classList.remove('active'));
            btn.classList.add('active');
            document.querySelector(`.tab-content[data-tab="${btn.dataset.tab}"]`).classList.add('active');
        });
    });

    document.getElementById('generate').addEventListener('click', async () => {
        const payload = {
            jdText: document.getElementById('jdText').value.trim(),
            jdUrl: document.getElementById('jdUrl').value.trim(),
            durationMonths: Number(document.getElementById('duration').value),
            dailyHours: Number(document.getElementById('hours').value),
            level: document.getElementById('level').value
        };
        if (!payload.jdText && !payload.jdUrl) payload.jdText = sampleJD;

        statusEl.textContent = '로드맵 생성 중...';
        setLoading(roadmapDiv, true);
        try {
            const res = await fetch(`${API_BASE}/api/roadmap/from-jd`, {
                method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload)
            });
            if (!res.ok) throw new Error('로드맵 생성 실패');
            const data = await res.json();
            renderRoadmap(data, roadmapDiv, roadmapMeta);
            statusEl.textContent = '완료! 저장 버튼으로 내 로드맵에 담을 수 있어요.';
            document.getElementById('saveRoadmap').onclick = () => saveRoadmap(data);
        } catch (e) {
            statusEl.textContent = e.message;
            roadmapDiv.innerHTML = '<p class="empty">로드맵 생성에 실패했습니다.</p>';
        } finally {
            setLoading(roadmapDiv, false);
        }
    });

    document.getElementById('fetchPosts').addEventListener('click', async () => {
        setLoading(postsDiv, true);
        try {
            const res = await fetch(`${API_BASE}/api/crawl/daily`);
            const data = await res.json();
            renderPosts(data, postsDiv);
        } catch (e) {
            postsDiv.innerHTML = `<p class="empty">${e.message}</p>`;
        } finally {
            setLoading(postsDiv, false);
        }
    });
}

async function saveRoadmap(data) {
    const token = localStorage.getItem('token');
    if (!token) { alert('로그인 후 저장할 수 있습니다.'); return; }
    const title = prompt('로드맵 제목을 입력하세요', '나의 맞춤 로드맵') || '나의 맞춤 로드맵';
    const payload = { title, progress: 0, roadmap: data };
    await fetch(`${API_BASE}/api/profile/roadmaps`, { method: 'POST', headers: { 'Content-Type': 'application/json', 'X-Auth-Token': token }, body: JSON.stringify(payload) });
    alert('저장되었습니다! 내 로드맵 페이지에서 확인하세요.');
}

function setLoading(el, isLoading) {
    el.classList.toggle('loading', isLoading);
    if (isLoading) el.innerHTML = '<p class="empty">잠시만 기다려주세요...</p>';
}

function renderRoadmap(data, container, metaEl) {
    container.classList.remove('empty');
    metaEl.textContent = `총 ${data.totalWeeks}주 · 하루 ${data.dailyHours}시간 권장`;
    const keywordBadges = (data.keywords || []).map(k => `<span class="badge">${k}</span>`).join('');
    const steps = data.steps.map(step => {
        const weeks = step.weeks.map(week => `
            <div class="card week">
                <h4>${week.weekNumber}주차</h4>
                <div class="badges">${week.topics.map(t => `<span class="badge">${t}</span>`).join('')}</div>
                <p>${week.mission}</p>
            </div>
        `).join('');
        return `<div class="card"><h3>${step.title} (${step.estimatedWeeks}주)</h3>${weeks}</div>`;
    }).join('');

    container.innerHTML = `<div class="card"><p class="eyebrow">추출 키워드</p><div class="badges">${keywordBadges}</div></div>${steps}`;
}

function renderPosts(posts, container) {
    container.classList.remove('empty');
    if (!posts.length) { container.innerHTML = '<p class="empty">크롤링된 공고가 없습니다.</p>'; return; }
    container.innerHTML = posts.map(post => `
        <div class="card">
            <p class="eyebrow">${post.company}</p>
            <h3>${post.role}</h3>
            <p>${post.location} · 마감 ${post.deadline}</p>
            <div class="badges">${post.keywords.map(k => `<span class="badge">${k}</span>`).join('')}</div>
            <a href="${post.url}" target="_blank" rel="noreferrer">공고 보기 →</a>
        </div>
    `).join('');
}

// Dashboard page
function initDashboard() {
    const savedDiv = document.getElementById('savedRoadmaps');
    const attendanceDiv = document.getElementById('attendance');
    document.getElementById('refreshRoadmaps').onclick = loadSaved;
    document.getElementById('attendanceCheck').onclick = checkAttendance;
    loadSaved();
    loadAttendance();

    async function loadSaved() {
        const token = localStorage.getItem('token');
        if (!token) { savedDiv.innerHTML = '<p class="empty">로그인 후 이용해주세요.</p>'; return; }
        setLoading(savedDiv, true);
        try {
            const res = await fetch(`${API_BASE}/api/profile/roadmaps`, { headers: { 'X-Auth-Token': token } });
            const data = await res.json();
            savedDiv.classList.remove('empty');
            savedDiv.innerHTML = data.map(r => `
                <div class="card">
                    <p class="eyebrow">${r.createdAt}</p>
                    <h3>${r.title}</h3>
                    <p>총 ${r.roadmap.totalWeeks}주 · 진행률 ${r.progress}%</p>
                </div>
            `).join('');
        } catch (e) {
            savedDiv.innerHTML = `<p class="empty">${e.message}</p>`;
        }
    }

    async function checkAttendance() {
        const token = localStorage.getItem('token');
        if (!token) { alert('로그인 후 체크 가능합니다.'); return; }
        await fetch(`${API_BASE}/api/attendance/check`, { method: 'POST', headers: { 'X-Auth-Token': token } });
        loadAttendance();
    }

    async function loadAttendance() {
        const token = localStorage.getItem('token');
        if (!token) { attendanceDiv.innerHTML = '<p class="empty">로그인 후 이용해주세요.</p>'; return; }
        const res = await fetch(`${API_BASE}/api/attendance`, { headers: { 'X-Auth-Token': token } });
        const data = await res.json();
        attendanceDiv.classList.remove('empty');
        attendanceDiv.innerHTML = `<h3>출석 ${data.streak}일 연속</h3><p>${Array.from(data.checkedDates || []).join(', ')}</p>`;
    }
}

// Community page
function initCommunity() {
    const postsDiv = document.getElementById('communityPosts');
    document.getElementById('refreshPosts').onclick = loadCommunity;
    document.getElementById('postSubmit').onclick = async () => {
        const token = localStorage.getItem('token');
        if (!token) { alert('로그인 후 글을 작성할 수 있습니다.'); return; }
        const title = document.getElementById('postTitle').value;
        const content = document.getElementById('postContent').value;
        try {
            await fetch(`${API_BASE}/api/community/posts`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json', 'X-Auth-Token': token },
                body: JSON.stringify({ title, content })
            });
            document.getElementById('postStatus').textContent = '등록 완료';
            loadCommunity();
        } catch (e) {
            document.getElementById('postStatus').textContent = '등록 실패';
        }
    };
    loadCommunity();

    async function loadCommunity() {
        setLoading(postsDiv, true);
        const res = await fetch(`${API_BASE}/api/community/posts`);
        const data = await res.json();
        postsDiv.classList.remove('empty');
        postsDiv.innerHTML = data.map(p => `
            <div class="card">
                <p class="eyebrow">${p.author}</p>
                <h3>${p.title}</h3>
                <p>${p.content}</p>
            </div>
        `).join('');
        setLoading(postsDiv, false);
    }
}
